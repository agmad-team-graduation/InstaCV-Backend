import string
import numpy as np
from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline, AutoTokenizer

# Initialize FastAPI
app = FastAPI()

# Load models and tokenizers
knowledge_model_name = "jjzha/jobbert_knowledge_extraction"
knowledge_tokenizer = AutoTokenizer.from_pretrained(knowledge_model_name)
knowledge_nlp = pipeline(
    model=knowledge_model_name,
    tokenizer=knowledge_tokenizer,
    aggregation_strategy="first",
)

skill_model_name = "jjzha/jobbert_skill_extraction"
skill_tokenizer = AutoTokenizer.from_pretrained(skill_model_name)
skill_nlp = pipeline(
    model=skill_model_name,
    tokenizer=skill_tokenizer,
    aggregation_strategy="first",
)


class TextInput(BaseModel):
    jobDescription: str


def convert_from_numpy(predictions):
    for pred in predictions:
        for key, value in pred.items():
            if isinstance(value, (np.float32, np.int32, np.int64)):
                pred[key] = float(value)
    return predictions


def merge_BI_and_get_results(predictions):
    results, curSkill, curScore, curNoWords = [], "", 0, 0
    for pred in predictions:
        if pred["entity_group"] == "B":
            if curSkill:
                results.append(
                    {"name": curSkill.strip(), "confidence": curScore / curNoWords}
                )
            curSkill, curScore, curNoWords = pred["word"], pred["score"], 1
        else:
            curSkill += " " + pred["word"]
            curScore += pred["score"]
            curNoWords += 1
    if curSkill:
        results.append({"name": curSkill.strip(), "confidence": curScore / curNoWords})
    return results


def chunk_text(text, tokenizer, max_length=500, overlap=100):
    """
    Uses the tokenizer's built-in overflow mechanism to split `text` into
    chunks of at most `max_length` tokens, each overlapping the previous
    by `overlap` tokens.
    """
    enc = tokenizer(
        text,
        truncation=True,
        max_length=max_length,
        stride=overlap,
        return_overflowing_tokens=True,
        return_special_tokens_mask=False,
    )
    chunks = []
    for ids in enc["input_ids"]:
        # decode each chunk back to string
        chunks.append(tokenizer.decode(ids, skip_special_tokens=True))
    return chunks


@app.post("/predict_knowledge")
def predict_knowledge(input_data: TextInput):
    # Clean non-printable chars
    text = "".join(filter(lambda x: x in string.printable, input_data.jobDescription))
    chunks = chunk_text(text, knowledge_tokenizer)
    all_preds = []
    for chunk in chunks:
        preds = knowledge_nlp(chunk)
        all_preds.extend(convert_from_numpy(preds))
    return {"knowledge_predictions": merge_BI_and_get_results(all_preds)}


@app.post("/predict_skills")
def predict_skills(input_data: TextInput):
    text = "".join(filter(lambda x: x in string.printable, input_data.jobDescription))
    chunks = chunk_text(text, skill_tokenizer)
    all_preds = []
    for chunk in chunks:
        preds = skill_nlp(chunk)
        all_preds.extend(convert_from_numpy(preds))
    return {"skills_predictions": merge_BI_and_get_results(all_preds)}

# Run with:
# uvicorn main:app --host 0.0.0.0 --port 8000
