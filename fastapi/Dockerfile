# Use an official Python image
FROM python:3.11

# Set the working directory inside the container
WORKDIR /app

# Copy the FastAPI app files to the container
COPY . /app

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Expose the FastAPI default port
EXPOSE 8000

# Command to run FastAPI with Uvicorn
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
