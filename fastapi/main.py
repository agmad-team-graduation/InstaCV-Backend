import numpy as np
from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline

# Load models and tokenizers
knowledge_nlp = pipeline(model="jjzha/jobbert_knowledge_extraction", aggregation_strategy="first")
skill_nlp = pipeline(model="jjzha/jobbert_skill_extraction", aggregation_strategy="first")

app = FastAPI()


class TextInput(BaseModel):
    jobDescription: str


@app.post("/predict_knowledge")
def predict_knowledge(input_data: TextInput):
    predictions = knowledge_nlp(input_data.jobDescription)
    for pred in predictions:
        for key, value in pred.items():
            if isinstance(value, (np.float32, np.int32, np.int64)):  # Handle NumPy numeric types
                pred[key] = float(value)
    results = []
    for pred in predictions:
        results.append(pred['word'])
    return {"knowledge_predictions": results}


@app.post("/predict_skills")
def predict_skills(input_data: TextInput):
    predictions = skill_nlp(input_data.jobDescription)
    for pred in predictions:
        for key, value in pred.items():
            if isinstance(value, (np.float32, np.int32, np.int64)):  # Handle NumPy numeric types
                pred[key] = float(value)
    results = []
    for pred in predictions:
        results.append(pred['word'])
    return {"skills_predictions": results}

# Run with:
# uvicorn main:app --host 0.0.0.0 --port 8000
