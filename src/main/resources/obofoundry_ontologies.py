import requests

r = requests.get('http://www.obofoundry.org/registry/ontologies.jsonld')
ontologies = r.json()['ontologies']
for ontology in ontologies:
    try:
        if 'is_obsolete' not in ontology.keys() or ontology['is_obsolete'] != True:
            print("{}={}".format(ontology['id'], ontology['ontology_purl']))
    except:
        pass
