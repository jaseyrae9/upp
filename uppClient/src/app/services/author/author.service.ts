import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthorService {

  constructor(private httpClient: HttpClient) { }

  getTasks() {
    return this.httpClient.get('http://localhost:8080/author/tasks') as Observable<any>;
  }

  getForm(processInstance: string) {
    return this.httpClient.get('http://localhost:8080/author/fields/'.concat(processInstance)) as Observable<any>;
  }

  submit(dto, taskId) {
    return this.httpClient.post('http://localhost:8080/author/submit/'.concat(taskId), dto) as Observable<any>;
  }

}
