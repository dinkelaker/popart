///////////////////////////////////////////////////////////////////////////////
// Copyright 2008-2015, Technische Universitaet Darmstadt (TUD), Germany
//
// The TUD licenses this file to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
///////////////////////////////////////////////////////////////////////////////
package de.tud.stg.example.aosd2010.casestudy1.DynamicWS;

import java.util.ArrayList;
import java.util.List;

public class BookWebService {

	public List<Book> books = new ArrayList<Book>();

	public BookWebService() {
		super();
		Author author1 = new Author("Fergal", "Dearle"," " );
		books.add(new Book("Groovy for Domain-Specific Languages", author1, "May 2010", "978-1-847196-90-3"));
		Author author2 = new Author("Jean-Luc","MONTAGNER"," ");
		books.add(new Book("Réseaux d'entreprise par la pratique", author2, "April 19, 2006", "2-212-11258-0"));
	}

	public BookWebService(List<Book> books) {
		this.books = books;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}
	
	public void addBook(Book book){
		books.add(book);
	}
	
	public List<Book> findBooksByAuthorLastName(String authorname){
		ArrayList<Book> selectedBooks = new ArrayList<Book>();
		for(Book book : books){
			if(book.getAuthor().getAuthorLastName().equalsIgnoreCase(authorname))
				selectedBooks.add(book);
		}
		return selectedBooks;
	}
	
}
