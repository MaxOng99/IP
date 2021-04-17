"""
Apply feature enhancements and drop any redundant data
based on initial data analaysis.
"""
import yaml
import numpy as np

# pylint: disable=C0116
# pylint: disable=R0201
# pylint: disable=C0103
class Preprocessor:
	"""
	- df: Dataframe of a participant
	- dataset_type: Either 'keystroke' or 'reaction' (each has different preprocessing)
	- mapping: Mapping of Strings to preprocessing steps (run diff preprocessing based on params.yaml)
	"""
	def __init__(self, df, dataset_type):
		self.df = df
		self.dataset_type = dataset_type
		self.params = self.load_params()
		self.mapping = self.step_to_function_mapping()
		self.preprocess()

	def step_to_function_mapping(self):
		mapping = {
			'add_temporal_features': self.add_temporal_features,
			'add_statistical_features': self.add_statistical_features,
			'drop_redundant_features': self.drop_redundant_features
		}

		return mapping

	def preprocess(self):
		preprocess_steps = self.params['steps']

		for step in preprocess_steps:
			self.mapping[step]()

		self.reorder_columns()

	def load_params(self):
		with open("pipeline/params.yaml", 'r') as config_file:
			return  yaml.safe_load(config_file)['preprocess']

	def add_temporal_features(self):

		if self.dataset_type == "reaction":
			for i, row in self.df.iterrows():
				new_row = self.reorder_timestamp(row)
				self.df.at[i, :] = new_row

		"""
		# Add keyhold features
		for i in range(1, 5):
			self.df.loc[:, f'keyhold{i}'] = self.df[f'upTimestamp{i}'] - self.df[f'downTimestamp{i}']
		"""
		
		# Add reaction features
		for j in range(1, 4):
			self.df.loc[:, f'intertap{j}'] = self.df[f'downTimestamp{j+1}'] - self.df[f'upTimestamp{j}']

	# Used for adding temporal features for reaction dataset
	def reorder_timestamp(self, row):

		temp = row.copy(deep=True)

		for i in range(1, 5):
			new_down = temp[f'downTimestamp{i}']
			new_up = temp[f'upTimestamp{i}']

			if row[f'tapCount{i}'] == 0:
				row['downTimestamp1'] = new_down
				row['upTimestamp1'] = new_up

			elif row[f'tapCount{i}'] == 1:
				row['downTimestamp2'] = new_down
				row['upTimestamp2'] = new_up

			elif row[f'tapCount{i}'] == 2:
				row['downTimestamp3'] = new_down
				row['upTimestamp3'] = new_up

			elif row[f'tapCount{i}'] == 3:
				row['downTimestamp4'] = new_down
				row['upTimestamp4'] = new_up

		return row

	def drop_redundant_features(self):

		redundant_columns_regex = '''user|date|sample_id|tapCount.*|index.*|up.*
		|.*Timestamp.*|keyhold.*|avgkeyhold'''

		new_cols = self.df.columns.drop(list(self.df.filter(regex=redundant_columns_regex)))

		self.df = self.df[new_cols]

	def add_statistical_features(self):
		features = self.params['statistical_features']

		for feature in features:
			if feature == "intertap":
				temp = [f'{feature}{i}' for i in range(1, 4)]
			else:
				temp = [f'{feature}{i}' for i in range(1, 5)]
			self.df.loc[:, f'avg{feature}'] = self.df[temp].apply(np.mean, axis=1)

	def reorder_columns(self):
		column_position = ['downPressure1', 'downX1', 'downY1',
		'downPressure2', 'downX2', 'downY2',
		'downPressure3', 'downX3', 'downY3',
		'downPressure4', 'downX4', 'downY4',
		'intertap1', 'intertap2', 'intertap3',
		'avgdownX', 'avgdownY', 'avgdownPressure', 'avgintertap']

		self.df = self.df.reindex(columns=column_position)
