import { Component, OnInit } from '@angular/core';
import { JournalService } from 'src/app/services/journal/journal.service';
import { Journal } from 'src/app/model/journal/journal';

@Component({
  selector: 'app-all-journals-page',
  templateUrl: './all-journals-page.component.html',
  styleUrls: ['./all-journals-page.component.css']
})
export class AllJournalsPageComponent implements OnInit {
  journals: Journal[] = [];

  constructor(private journalService: JournalService) { }

  ngOnInit() {
    this.loadJournals();
  }

  loadJournals() {
    this.journalService.getAll().subscribe(data => {
      this.journals = data;
      console.log('data: ', data);
    });
  }

}
