import { Component, OnInit, Input } from '@angular/core';
import { Paper } from 'src/app/model/journal/paper';
import { TokenStorageService } from 'src/app/auth/token-storage.service';
import { DataService } from 'src/app/services/data.service';
import { PaperService } from 'src/app/services/journal/paper.service';
import { HttpErrorResponse } from '@angular/common/http';

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
  errorMessage = '';
  constructor(public tokenService: TokenStorageService,
              private dataService: DataService,
              private paperService: PaperService) { }

  ngOnInit() {
    console.log('paper: ', this.paper);
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

  download() {
    console.log('rad id' , this.paper.id);
    this.paperService.getPaper(this.paper.id).subscribe(
      res => {
        console.log(res);
        const downloadLink = document.createElement('a');
        const fileName = 'sample.pdf';
        downloadLink.href = res;
        downloadLink.download = fileName;
        downloadLink.click();
      },
      (err: HttpErrorResponse) => {
        console.log('Error submit');
        console.log('err: ', err);
        this.errorMessage = err.error.message;
      }
    );
  }
}
