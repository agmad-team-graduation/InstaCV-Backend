from fastapi import FastAPI
from skills_extraction.skills_extraction import app as skills_app
from semantic_similarity.semantic_similarity import app as similarity_app

app = FastAPI()

# Mount the two apps under different routes
app.mount("/skills", skills_app)
app.mount("/similarity", similarity_app)

# if __name__ == "__main__":
#     import uvicorn
#     uvicorn.run("main:app", host="0.0.0.0", port=7860, reload=True)