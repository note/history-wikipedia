#!/usr/bin/python

from wikipedia_models import *

# for each WW2 article counts how many references to all others articles and saves results to simpleGraph table
def saveToSimpleGraph():
	query = WW2Article.select()
	articles = query.execute()
	articlesList = [article for article in articles] # copy to list

	for fromArticle in articlesList:
		for toArticle in articlesList:
			if fromArticle != toArticle:
				count = fromArticle.text.count(toArticle.title.replace("_", " ")) # wikipedia convention - in titles spaces are replaces with underscores
				insert = SimpleGraph.insert(from_person = fromArticle.id, from_name = fromArticle.title, to_person = toArticle.id, to_name = toArticle.title, weight = count)
				insert.execute()


if __name__ == "__main__":
	saveToSimpleGraph()