import { Component, OnInit } from '@angular/core';
import { JournalService } from 'src/app/services/journal/journal.service';

@Component({
  selector: 'app-editor-tasks',
  templateUrl: './editor-tasks.component.html',
  styleUrls: ['./editor-tasks.component.css']
})
export class EditorTasksComponent implements OnInit {
  private tasks = [];

  constructor(private journalService: JournalService) {
    const x = this.journalService.getEditorTasks();

    x.subscribe(
      res => {
        console.log('res: ', res);
        this.tasks = res;
      },
      err => {
        console.log('Error occured');
      }
    );
  }

  ngOnInit() {
  }

}
