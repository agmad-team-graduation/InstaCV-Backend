from typing import Optional, List
from pydantic import BaseModel
from .models import *


# Needed for testing
class SkillPairInput(BaseModel):
    skill1: str
    skill2: str


# Skill Matching
class SkillsMatchingRequest(BaseModel):
    jobSkills: List[JobSkill]
    userSkills: List[UserSkill]
    similarityThreshold: Optional[float] = 0.7


class MatchedSkill(BaseModel):
    jobSkill: JobSkill
    userSkill: UserSkill
    similarity: float


class MatchingSkillsResponse(BaseModel):
    matchedSkills: List[MatchedSkill]
    unmatchedJobSkills: List[JobSkill]
    unmatchedUserSkills: List[UserSkill]


# Project Matching

class ProjectsMatchingRequest(BaseModel):
    jobSkills: List[JobSkill]
    projects: List[Project]
    similarityThreshold: Optional[float] = 0.7


class MatchedProjectSkill(BaseModel):
    jobSkill: JobSkill
    projectSkill: ProjectSkill
    similarity: float


class MatchedProject(BaseModel):
    project: Project
    matchedSkills: List[MatchedProjectSkill]
    matchedSkillsCount: int


class MatchingProjectsResponse(BaseModel):
    allAnalyzedProjects: List[MatchedProject]
