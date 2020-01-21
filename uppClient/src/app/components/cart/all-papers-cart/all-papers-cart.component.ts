import { Component, OnInit } from '@angular/core';
import { Author } from 'src/app/model/user/author';
import { PaperService } from 'src/app/services/journal/paper.service';
import { HttpErrorResponse } from '@angular/common/http';
import { PaymentRequestDTO } from 'src/app/model/journal/paymentRequestDTO';
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-papers-cart',
  templateUrl: './all-papers-cart.component.html',
  styleUrls: ['./all-papers-cart.component.css']
})
export class AllPapersCartComponent implements OnInit {
  items = [];
  authors = [];
  paymentRequestDTO = new PaymentRequestDTO();

  constructor(private paperService: PaperService,
              private router: Router) { }

  ngOnInit() {
    this.getCartItems();
  }

  getCartItems() {
    if (sessionStorage.length > 0) {
      this.items = JSON.parse(sessionStorage.getItem('itemsInCart'));
      //
      if(this.items == null) {
        this.items = [];
      }
      console.log('items', this.items);
      for (const item of this.items) {
        // item.author.id
        if (!this.containsId(this.authors, item.author.id)) {
          this.authors.push(new Author(item.author.id, item.author.username, item.author.firstName, item.author.lastName));
        }
      }
      console.log('autori: ', this.authors);
    }
  }

  containsId(authors: Author[], authorId: number) {
    for(const author of authors) {
      if (author.id === authorId) {
        return true;
        break;
      }
    }
    return false;
  }

  buyFromThisSeller(author: Author) {
    console.log('autor: ', author);
    for (const item of this.items) {
      if(item.author.id === author.id) {
        this.paymentRequestDTO.papers.push(item);
      }
    }
    console.log('ti radovi:' , this.paymentRequestDTO);
    this.paperService.buyFromThisSeller(this.paymentRequestDTO).subscribe(
      data => {
        console.log(data);
        window.location.href = data;
      },
      (err: HttpErrorResponse) => {
       console.log(err);
      }
    );
  }

}
