import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from 'src/app/auth/token-storage.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css', './../../../shared/inputField.css']
})
export class NavigationComponent implements OnInit {
  public loggedIn: Boolean;
  public username: String;

  constructor(private router: Router,
              public tokenService: TokenStorageService) {
    this.loggedIn = tokenService.checkIsLoggedIn();
    this.tokenService.logggedInEmitter.subscribe(loggedIn => {
      this.loggedIn = loggedIn;
    });
    this.username = tokenService.getUsername();
    console.log('username->', this.username);
    this.tokenService.usernameEmitter.subscribe(username => this.username = username);
  }

  ngOnInit() {
  }

  logout() {
    this.router.navigate(['']);
    this.tokenService.signOut();
  }

  functionOnWhichRedirectShouldHappen() {
    console.log('pozvana funkcija');
    this.router.navigate(['/register']);
  }
}
