from fastapi import FastAPI, Header
from firebase_admin.auth import InvalidIdTokenError
from pydantic import BaseModel
from typing import Mapping, List
import pickle

## Global Variables
user_model_mapping = dict()

## Data Classes
class TouchGestureData(BaseModel):
	data: List[Mapping[str, float]]
	date: str
	sample_id: int

class Request(BaseModel):
	request: Mapping[int, List[TouchGestureData]]

class Response(BaseModel):
	#response: Mapping[int, int] 
	response: str

## Helper functions
def init_models():
	for id in range(1, 5):
		with open(f'models/model{id}.pkl') as f:
			user_model_mapping[id] = pickle.load(f)

def authenticate(jwt_token):
	decoded_token = auth.verify_id_token(jwt_token)
	return decoded_token['uid']

## App Initialisation
app = FastAPI()
#init_models()

## API Routings
@app.get('/')
def index():
	return {'key': 'value'}

@app.post('/predict', response_model=Response)
def predict(request: Request, jwt_token = Header(None)):

	try:
		uid = authenticate(jwt_token)
		#model = user_model_mapping[uid]
		#result = dict()
		#sresponse = dict()

		"""
		for uid, touch_gesture_data_list in request:
			# result[uid] = [model.predict(touch_gesture.data) for touch_gesture in touch_gesture_data_list]

		for uid, predictions in result:
			true_predictions = [x if x == 1 for x in predictions]
			response[uid] = len(true_predictions)

		return response
		"""
		return "auth success"

	except InvalidIdTokenError:
		print("Authroization failed")


