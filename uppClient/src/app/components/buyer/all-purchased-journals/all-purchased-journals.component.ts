import { Component, OnInit, Input } from '@angular/core';
import { Journal } from 'src/app/model/journal/journal';
import { JournalService } from 'src/app/services/journal/journal.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-all-purchased-journals',
  templateUrl: './all-purchased-journals.component.html',
  styleUrls: ['./all-purchased-journals.component.css']
})
export class AllPurchasedJournalsComponent implements OnInit {
  journals: Journal[] = [];
  message: string;

  constructor(private journalService: JournalService,
              private dataService: DataService) { }

  ngOnInit() {
    this.dataService.currentMessage.subscribe(message => this.message = message);
    console.log('init u all purchased', this.message);
    this.loadJournals();
  }

  loadJournals() {
    this.journalService.getAllPurchased().subscribe(data => {
      this.journals = data;
      console.log('kupljeni casopisi: ', data);
      this.newMessage();

    });
  }

  newMessage() {
    this.dataService.changeMessage('true');
    console.log('new message u all purchased journals', this.message);
  }

}
