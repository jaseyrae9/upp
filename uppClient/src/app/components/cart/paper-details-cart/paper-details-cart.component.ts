import { Component, OnInit, Input } from '@angular/core';
import { Paper } from 'src/app/model/journal/paper';

@Component({
  selector: 'app-paper-details-cart',
  templateUrl: './paper-details-cart.component.html',
  styleUrls: ['./paper-details-cart.component.css']
})
export class PaperDetailsCartComponent implements OnInit {
  @Input() paper: Paper;

  constructor() { }

  ngOnInit() {
  }

}
