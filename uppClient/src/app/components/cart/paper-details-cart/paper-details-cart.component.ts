import { Component, OnInit, Input } from '@angular/core';
import { Paper } from 'src/app/model/journal/paper';
import { Journal } from 'src/app/model/journal/journal';

@Component({
  selector: 'app-paper-details-cart',
  templateUrl: './paper-details-cart.component.html',
  styleUrls: ['./paper-details-cart.component.css']
})
export class PaperDetailsCartComponent implements OnInit {
  @Input() journal: Journal;

  constructor() { }

  ngOnInit() {
  }

}
