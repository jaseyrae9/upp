import { Component, OnInit, Input } from '@angular/core';
import { JournalService } from 'src/app/services/journal/journal.service';
import { Journal } from 'src/app/model/journal/journal';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-all-journals-page',
  templateUrl: './all-journals-page.component.html',
  styleUrls: ['./all-journals-page.component.css']
})
export class AllJournalsPageComponent implements OnInit {
  journals: Journal[] = [];
  message: string;

  show: boolean;

  constructor(private journalService: JournalService,
              private dataService: DataService) { }

  ngOnInit() {
    this.dataService.currentMessage.subscribe(message => this.message = message);
    console.log('init u all journals', this.message);
    this.loadJournals();
  }

  loadJournals() {
    this.journalService.getAll().subscribe(data => {
      this.journals = data;
      console.log('data: ', data);
      this.newMessage();

    });
  }
  loadPurchasedJournals() {
    this.journalService.getAllPurchased().subscribe(data => {
      this.journals = data;
      console.log('kupljeni casopisi: ', data);
      this.newMessage();

    });
  }
  newMessage() {
    this.dataService.changeMessage('false');
    console.log('new message u all journals', this.message);
  }
}
