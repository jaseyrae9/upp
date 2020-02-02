import { Component, OnInit } from '@angular/core';
import { AuthorService } from 'src/app/services/author/author.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-all-tasks',
  templateUrl: './all-tasks.component.html',
  styleUrls: ['./all-tasks.component.css']
})
export class AllTasksComponent implements OnInit {
  tasks = [];
  errorMessage: String = '';

  constructor(private authorService: AuthorService) {
    this.authorService.getTasks().subscribe(
      res => {
        console.log('zadaci autora : ', res);
        this.tasks = res;
      },
      (err: HttpErrorResponse) => {
        console.log('Error prilikom ucitavanja zadataka');
        console.log('err: ', err);
        this.errorMessage = err.error.message;
      }
    );

  }

  ngOnInit() {

  }

}
