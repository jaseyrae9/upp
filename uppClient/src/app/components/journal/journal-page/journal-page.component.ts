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

  constructor(private journalService: JournalService,
              private route: ActivatedRoute,
    ) { }

  ngOnInit() {
    const journalId = this.route.snapshot.paramMap.get('id');
    this.journalId = +journalId; // + -> string u int

    this.getCompanie();
  }

  getCompanie() {
    this.journalService.getJournal(this.journalId).subscribe(
      (data) => {
        this.journal = data;
        console.log('Otvoren je casopis: ', this.journal);
      }
    );
  }

}
