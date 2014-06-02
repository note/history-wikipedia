#!/usr/bin/python

from wikipedia_models import *

# saves direct children of given category to WW2Article table
def getAndSaveChildren(categoryName):
	linksQuery = Categorylinks.select().where(Categorylinks.cl_to == categoryName)
	links = linksQuery.execute()

	for link in links:

		## we are interested only in direct children of given category
		## cl_type can also be "subcat" and "file"
		## if we want to traverse category recursively we need to handle subcat also.
		if link.cl_type == "page":
			page = Page.get(Page.page == link.cl_from)
			rev = Revision.get(Revision.rev_page == link.cl_from)
			text = Text.get(Text.old == rev.rev_text)
			articleInsert = WW2Article.insert(id = link.cl_from, title = page.page_title, text = text.old_text)
			articleInsert.execute()

if __name__ == "__main__":
	getAndSaveChildren("World_War_II_political_leaders")