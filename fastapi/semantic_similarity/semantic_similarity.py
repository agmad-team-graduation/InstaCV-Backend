import uvicorn
from fastapi import FastAPI
from sentence_transformers import SentenceTransformer, util
from .models import *
from .DTOs import *

app = FastAPI(title="Skill Embedding API")

# Load model once at startup
# You can replace this with, https://huggingface.co/burakkececi/bert-software-engineering ?
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")


# --- Endpoints ---

@app.post("/similarity")
def similarity(pair: SkillPairInput):
    emb1 = model.encode(pair.skill1, convert_to_tensor=True)
    emb2 = model.encode(pair.skill2, convert_to_tensor=True)
    sim_score = util.cos_sim(emb1, emb2).item()
    return {
        "skill1": pair.skill1,
        "skill2": pair.skill2,
        "similarity": sim_score
    }


@app.post("/match-skills", response_model=MatchingSkillsResponse)
def match_skills(req: SkillsMatchingRequest):
    job_skills = req.jobSkills
    user_skills = req.userSkills
    threshold = req.similarityThreshold

    job_texts = [j.skill for j in job_skills]
    user_texts = [u.skill for u in user_skills]

    job_embeddings = model.encode(job_texts, convert_to_tensor=True)
    user_embeddings = model.encode(user_texts, convert_to_tensor=True)

    similarity_matrix = util.cos_sim(job_embeddings, user_embeddings)

    matched = []
    unmatched_job_indices = set(range(len(job_skills)))
    unmatched_user_indices = set(range(len(user_skills)))

    for i, job_row in enumerate(similarity_matrix):
        best_idx = int(job_row.argmax())
        best_score = float(job_row[best_idx])

        if best_score >= threshold:
            matched.append(MatchedSkill(
                jobSkill=job_skills[i],
                userSkill=user_skills[best_idx],
                similarity=best_score
            ))
            unmatched_job_indices.discard(i)
            unmatched_user_indices.discard(best_idx)

    unmatched_jobs = [job_skills[i] for i in unmatched_job_indices]
    unmatched_users = [user_skills[i] for i in unmatched_user_indices]

    return MatchingSkillsResponse(
        matchedSkills=matched,
        unmatchedJobSkills=unmatched_jobs,
        unmatchedUserSkills=unmatched_users
    )


@app.post("/match-projects-skills", response_model=MatchingProjectsResponse)
def match_projects_skills(req: ProjectsMatchingRequest):
    job_skills = req.jobSkills
    projects = req.projects
    threshold = req.similarityThreshold

    job_texts = [job.skill for job in job_skills]
    job_embeddings = model.encode(job_texts, convert_to_tensor=True)

    matched_projects: List[MatchedProject] = []

    for project in projects:
        project_texts = [ps.skill for ps in project.skills]
        project_embeddings = model.encode(project_texts, convert_to_tensor=True)

        similarity_matrix = util.cos_sim(job_embeddings, project_embeddings)

        matched_skills: List[MatchedProjectSkill] = []

        for i, job_skill in enumerate(job_skills):
            job_row = similarity_matrix[i]
            best_idx = int(job_row.argmax())
            best_score = float(job_row[best_idx])

            if best_score >= threshold:
                matched_skills.append(MatchedProjectSkill(
                    jobSkill=job_skill,
                    projectSkill=project.skills[best_idx],
                    similarity=best_score
                ))

        matched_projects.append(MatchedProject(
            project=project,
            matchedSkills=matched_skills,
            matchedSkillsCount=len(matched_skills)
        ))

    return MatchingProjectsResponse(allAnalyzedProjects=matched_projects)


# uvicorn semantic_similarity:app --host 0.0.0.0 --port 8001
# if __name__ == "__main__":
#     uvicorn.run(app, host="0.0.0.0", port=8001, reload=False)
