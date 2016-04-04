# S-ProcMiner
Process mining tool developed with apache spark

Lien pour l'API: http://elasticsearch-py.readthedocs.org/en/master/helpers.html?highlight=reindex
Lien pour le télachargement: https://pypi.python.org/pypi/elasticsearch/2.3.0


from elasticsearch.helpers import reindex
from elasticsearch import Elasticsearch

// es est une instance elasticsearch
es = Elasticsearch(['http://localhost:9200'])


reindex(es,'site','ahmed',query={"query": {"match": {"name": "gater"}}})