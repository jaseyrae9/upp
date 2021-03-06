import { Component, OnInit, Input} from '@angular/core';
import { Journal } from 'src/app/model/journal/journal';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-journal-basic-details',
  templateUrl: './journal-basic-details.component.html',
  styleUrls: ['./journal-basic-details.component.css']
})
export class JournalBasicDetailsComponent implements OnInit {
  @Input() journal: Journal;

  constructor() { }

  ngOnInit() {
  }

}
