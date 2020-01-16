import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class JournalService {

  constructor(private httpClient: HttpClient) { }
  addJournal(journal, taskId) {
    return this.httpClient.post('http://localhost:8080/journal/post/'.concat(taskId), journal) as Observable<any>;
  }

  getEditorTasks() {
    return this.httpClient.get('http://localhost:8080/journal/get/tasks') as Observable<any>;
  }
  getTaskForm() {
    return this.httpClient.get('http://localhost:8080/journal/getForm') as Observable<any>;
  }

  getEditJournalTasks() {
    return this.httpClient.get('http://localhost:8080/journal/getEditJournalForm') as Observable<any>;
  }

  addEditorsAndReviewers(dto, taskId) {
    return this.httpClient.post('http://localhost:8080/journal/addEditorsAndReviewers/'.concat(taskId), dto) as Observable<any>;
  }

  editJournal(dto, taskId) {
    return this.httpClient.post('http://localhost:8080/journal/editJournal/'.concat(taskId), dto) as Observable<any>;
  }
}
