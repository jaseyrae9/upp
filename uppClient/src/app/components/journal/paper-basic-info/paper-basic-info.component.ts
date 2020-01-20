import { Component, OnInit, Input } from '@angular/core';
import { Paper } from 'src/app/model/journal/paper';

@Component({
  selector: 'app-paper-basic-info',
  templateUrl: './paper-basic-info.component.html',
  styleUrls: ['./paper-basic-info.component.css']
})
export class PaperBasicInfoComponent implements OnInit {
  @Input() paper: Paper;

  constructor() { }

  ngOnInit() {
  }

}
