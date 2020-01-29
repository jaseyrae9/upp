import { Component, OnInit, Input } from '@angular/core';
import { Paper } from 'src/app/model/journal/paper';
import { TokenStorageService } from 'src/app/auth/token-storage.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-paper-basic-info',
  templateUrl: './paper-basic-info.component.html',
  styleUrls: ['./paper-basic-info.component.css']
})
export class PaperBasicInfoComponent implements OnInit {
  @Input() paper: Paper;
  itemsInCart = [];
  items = [];
  message: string;

  constructor(public tokenService: TokenStorageService,
              private dataService: DataService) { }

  ngOnInit() {
    this.dataService.currentMessage.subscribe(message => this.message = message);
    console.log('init u paper basic info', this.message);
  }

  addToCart() {
    console.log('paper: ', this.paper);
    if (sessionStorage.length > 0) {
      this.items = JSON.parse(sessionStorage.getItem('itemsInCart'));
      if (this.items == null) {
        this.items = [];
      }
      console.log(this.items);
    }
      this.items.push(this.paper);
      sessionStorage.setItem('itemsInCart', JSON.stringify(this.items));
      alert('Uspešno dodato u korpu!');
  }
}
