import { Component, OnInit } from '@angular/core';
import { JournalService } from 'src/app/services/journal/journal.service';
import { Journal } from 'src/app/model/journal/journal';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-journal-page',
  templateUrl: './journal-page.component.html',
  styleUrls: ['./journal-page.component.css']
})
export class JournalPageComponent implements OnInit {
  journalId: number;
  journal: Journal = new Journal();

  itemsInCart = [];
  items = [];
  constructor(private journalService: JournalService,
              private route: ActivatedRoute,
    ) { }

  ngOnInit() {
    const journalId = this.route.snapshot.paramMap.get('id');
    this.journalId = +journalId; // + -> string u int

    this.getJournal();
  }

  getJournal() {
    this.journalService.getJournal(this.journalId).subscribe(
      (data) => {
        this.journal = data;
        console.log('Otvoren je casopis: ', this.journal);
      }
    );
  }
  addToCart() {
    console.log('journal: ', this.journal);
    if (sessionStorage.length > 0) {
      this.items = JSON.parse(sessionStorage.getItem('itemsInCart'));
      if (this.items == null) {
        this.items = [];
      }
      console.log(this.items);
    }
      this.items.push(this.journal);
      sessionStorage.setItem('itemsInCart', JSON.stringify(this.items));
      alert('Uspešno dodato u korpu!');
  }

}
