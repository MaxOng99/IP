"""
Encapsulates the process of converting raw json data
into Pandas compatible format before calling pd.read_json
"""
import json
import pandas as pd

class DataPreparer:
	"""
	- original_df = Dataframe that consists of all participant's touch gesture data.
	- users_df = Dictionary that contains mapping of user id to their Dataframe.
	"""
	def __init__(self, dataset):

		self.original_df = None
		self.users_df = None
		self.prepare_data(dataset)

	def prepare_data(self, dataset):
		"""Convert raw json data into compatible Pandas DataFrame format"""
		user_ids = list(dataset.keys())
		user_records = []

		for uid in user_ids:
			records = dataset[uid]

			for record in records:
				user_records.append(self.flatten_record(uid, record))

		prepared_df = pd.read_json(json.dumps(user_records))
		self.original_df = prepared_df
		self.users_df = {uid:group.copy(deep=True) for uid, group in prepared_df.groupby(by=['user'])}

	# pylint: disable=R0201
	def flatten_record(self, user, record):
		"""Flattens nested json data"""
		feature_map = dict()
		feature_map['user'] = user
		feature_map['date'] = record.date
		feature_map['sample_id'] = record.sample_id
		tap_data = record.data

		for index, single_tap in enumerate(tap_data):

			# Add tap count to each feature name to prevent overrides when JSON data is flattened
			new_dict = {f'{k}{index+1}':v for k,v in single_tap.items()}
			feature_map.update(new_dict)

		return feature_map

	# pylint: disable=R0201
	def get_uid_mapping(self):
		""" Transforms jwt_uid to application_uid """
		mapping = {'Ab95qgUrJDe0RNnT5A3ZIb5owEp1': 2,
		'HMav6t8045fX5g461hTtEVdgF4S2': 3,
		'QhMRTHq3FHcBDnJzOCcnmyltXKD2': 4,
		'd0BlMgUPQkQFPmAhVeCOaiWaqDB2': 1}
		return mapping
