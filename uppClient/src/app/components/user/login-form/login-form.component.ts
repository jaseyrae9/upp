import { Component, OnInit } from '@angular/core';
import { AuthLoginInfo } from 'src/app/auth/login-info';
import { UserService } from 'src/app/services/users/user.service';
import { JwtResponse } from 'src/app/auth/jwt-response';
import { TokenStorageService } from 'src/app/auth/token-storage.service';
import { TokenPayload } from 'src/app/model/token-payload';
import * as decode from 'jwt-decode';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {
  form: any = {};
  errorMessage: String = '';

  private loginInfo: AuthLoginInfo;

  constructor(private userService: UserService,
              private tokenStorage: TokenStorageService,
              private router: Router) { }

  ngOnInit() {
  }

  onLogin() {

    this.loginInfo = new AuthLoginInfo(
      this.form.username,
      this.form.password
    );

    this.userService.attemptAuth(this.loginInfo).subscribe(
      (data: JwtResponse) => {
        this.tokenStorage.saveUsername(this.form.username);
          this.tokenStorage.saveToken(data.token);
          const tokenPayload: TokenPayload = decode(data.token);
          this.tokenStorage.saveRoles(tokenPayload.roles);
          console.log(tokenPayload);
          this.router.navigate(['']);
      },
      (err: HttpErrorResponse) => {
        console.log('Error occured');
        console.log('err: ', err);
        this.errorMessage = err.error.message;

      }

    );

  }

}
