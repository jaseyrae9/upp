import { Component, OnInit, Input } from '@angular/core';
import { Edition } from 'src/app/model/journal/edition';

@Component({
  selector: 'app-edition-basic-info',
  templateUrl: './edition-basic-info.component.html',
  styleUrls: ['./edition-basic-info.component.css']
})
export class EditionBasicInfoComponent implements OnInit {
  @Input() edition: Edition;
  constructor() { }

  ngOnInit() {
    console.log('edition: ', this.edition);
  }

}
