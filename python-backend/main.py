from fastapi import FastAPI, Header
from firebase_amdin import auth


app = FastAPI()

@app.get('/')
def index():
	return {'key': 'value'}

@app.get('/auth')
def authenticate(*, jwt_token = Header(None)):
	decoded_token = auth.verify_id_token(jwt_token )
	uid = decoded_token['uid']
	return {"Authorization": jwt_token}



