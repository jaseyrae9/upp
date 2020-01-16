import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthLoginInfo } from 'src/app/auth/login-info';
import { JwtResponse } from 'src/app/auth/jwt-response';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private loginUrl = 'http://localhost:8080/register/login';

  constructor(private httpClient: HttpClient) { }

  registerUser(user, taskId) {
    return this.httpClient.post('http://localhost:8080/register/post/'.concat(taskId), user) as Observable<any>;
  }

  attemptAuth(credentials: AuthLoginInfo): Observable<JwtResponse> {
    console.log(credentials);
    return this.httpClient.post<JwtResponse>(this.loginUrl, credentials, httpOptions);
  }
}
