"""
Responsible for evaluating a chosen ml algorithm
based on EER, FAR and FRR.
"""
import numpy as np
from sklearn import metrics
import pandas as pd
import google.cloud.logging
import logging

# pylint: disable=C0116
# pylint: disable=R0201
class Evaluator():

	client = google.cloud.logging.Client()
	client.get_default_handler()
	client.setup_logging()

	"""
	- model: model to be evaluated.
	- users_df: Dictionary of user_id to their respective Dataframe.
	- uid: User id of the legitimate participant
	"""
	def __init__(self, model, users_df, legitimate_uid):
		self.model = model
		self.users_df = users_df
		self.uid = legitimate_uid

	def calculate_far(self, y_true, y_pred):
		cm = metrics.confusion_matrix(y_true, y_pred)
		fp = cm[0, 1]
		tn = cm[0, 0]
		far = 0 if (fp+tn) == 0 else fp/(fp+tn)

		return far

	def calculate_frr(self, y_true, y_pred):
		cm = metrics.confusion_matrix(y_true, y_pred)
		fn = cm[1, 0]
		tp = cm[1, 1]
		frr = 0 if (fn+tp) == 0 else fn/(fn+tp)

		return frr

	def calculate_eer(self, y_true, y_pred_score):
		fpr, fnr, _ = metrics.det_curve(y_true, y_pred_score)
		eer1 = fpr[np.nanargmin(np.absolute(fnr - fpr))]
		eer2 = fnr[np.nanargmin(np.absolute(fnr - fpr))]
		eer = (eer1 + eer2)/2

		return eer

	def get_prediction_results(self, uid):

		X = self.users_df[uid]
		logging.info(X)
		y_pred = self.model.predict(X)
		accepted_predictions = [prediction for prediction in y_pred if prediction == 1.0]

		return len(accepted_predictions)

	def evaluate(self):

		legitimate_df = self.users_df[self.uid]
		imposters = [imposter_df for uid, imposter_df in self.users_df.items() if uid != self.uid]
		imposters_df = pd.concat(imposters, axis=0, ignore_index=True)

		X = pd.concat([legitimate_df, imposters_df], axis=0, ignore_index=True)
		y = np.concatenate([np.repeat(1.0, len(legitimate_df)), np.repeat(-1.0, len(imposters_df))])
		y_predict = self.model.predict(X)
		y_predict_score = self.model.decision_function(X)

		far = self.calculate_far(y, y_predict)
		frr = self.calculate_frr(y, y_predict)
		eer = self.calculate_eer(y, y_predict_score)

		return (far, frr, eer)
