import { Component, OnInit } from '@angular/core';
import { Editor } from 'src/app/model/user/editor';
import { PaperService } from 'src/app/services/journal/paper.service';
import { HttpErrorResponse } from '@angular/common/http';
import { PaymentRequestDTO } from 'src/app/model/journal/paymentRequestDTO';
import { Router } from '@angular/router';
import { TokenStorageService } from 'src/app/auth/token-storage.service';

@Component({
  selector: 'app-all-papers-cart',
  templateUrl: './all-papers-cart.component.html',
  styleUrls: ['./all-papers-cart.component.css']
})
export class AllPapersCartComponent implements OnInit {
  items = [];
  editors = [];
  paymentRequestDTO = new PaymentRequestDTO();

  constructor(private paperService: PaperService,
              private router: Router,
              public tokenService: TokenStorageService) { }

  ngOnInit() {
    this.getCartItems();
  }

  getCartItems() {
    if (sessionStorage.length > 0) {
      this.items = JSON.parse(sessionStorage.getItem('itemsInCart'));
      //
      if (this.items == null) {
        this.items = [];
      }
      console.log('items', this.items);
      for (const item of this.items) {
        console.log('item', item);
        // item.author.id
        if (!this.containsId(this.editors, item.editorInChief.id)) {
          // tslint:disable-next-line: max-line-length
          this.editors.push(new Editor(item.editorInChief.id, item.editorInChief.username, item.editorInChief.firstName, item.editorInChief.lastName));
        }
      }
      console.log('editori: ', this.editors);
    }
  }

  containsId(editors: Editor[], editorId: number) {
    for (const editor of editors) {
      if (editor.id === editorId) {
        return true;
        break;
      }
    }
    return false;
  }

  buyFromThisSeller(editor: Editor) {
    console.log('autor: ', editor);
    for (const item of this.items) {
      if (item.editorInChief.id === editor.id) {
        this.paymentRequestDTO.journals.push(item);
      }
    }
    console.log('ti radovi:' , this.paymentRequestDTO);
    this.paperService.buyFromThisSeller(this.paymentRequestDTO).subscribe(
      data => {
        console.log(data);
        for (const journal of this.paymentRequestDTO.journals) {
           for (const item of this.items) {
            if (journal.id === item.id) {
              const index = this.items.findIndex(item => item.id === journal.id);
              this.items.splice(index, 1);
            }
           }
        }
        console.log('items, ', this.items);
        sessionStorage.setItem('itemsInCart', JSON.stringify(this.items));
        window.location.href = data;
      },
      (err: HttpErrorResponse) => {
       console.log(err);
      }
    );
  }

}
