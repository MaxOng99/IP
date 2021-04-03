cd python-backend/

pip install -r scripts/requirements.txt

cd src/main
pylint_runner -rcfile=scripts/.pylintrc

cd ../tests
python -m pytest