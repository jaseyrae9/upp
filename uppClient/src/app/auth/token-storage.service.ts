import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Role } from 'src/app/model/role';

const TOKEN_KEY = 'AuthToken';
const USERNAME_KEY = 'username';
const ROLES_KEY = 'roles';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private isLoggedIn = new Subject<Boolean>();
  private username = new Subject<String>();
  private roles = new Subject<Role[]>();

  public isEditor = false;
  public isEditorInChief = false;
  public isAdmin = false;
  public isCustomer = false;
  public isVistor = false;
  public isBuyer = false;
  public isAuthor = false;

  public logggedInEmitter = this.isLoggedIn.asObservable();
  public usernameEmitter = this.username.asObservable();
  public rolesEmitter = this.roles.asObservable();

  constructor() {
    this.isLoggedIn.next(false);
    this.roles.next(null);
    this.checkRoles();
  }

  loggedInEmitChange(loggedIn: Boolean) {
    this.isLoggedIn.next(loggedIn);
  }

  usernameEmitChange(username: String) {
    this.username.next(username);
  }

  rolesEmitChange(roles: Role[]) {
    this.roles.next(roles);
  }

  signOut() {
    window.sessionStorage.clear();
    this.loggedInEmitChange(false);
    this.usernameEmitChange(null);
    this.rolesEmitChange(null);
    this.checkRoles();
  }

  public saveToken(token: string) {
    window.sessionStorage.setItem(TOKEN_KEY, token);
    this.loggedInEmitChange(true);
  }

  public saveUsername(username: string) {
    window.sessionStorage.setItem(USERNAME_KEY, username);
    this.usernameEmitChange(username);
  }

  public saveRoles(roles: Role[]) {
    window.sessionStorage.setItem(ROLES_KEY, JSON.stringify(roles));
    this.checkRoles();
    this.rolesEmitChange(roles);
  }



  public checkRoles() {
    this.isEditor = false;
    this.isEditorInChief = false;
    this.isAdmin = false;
    this.isCustomer = false;
    this.isVistor  = false;
    this.isBuyer = false;
    this.isAuthor = false;
    const roles = this.getRoles();
    if (roles) {
      for (const role of roles) {
        if ( role.authority === 'ROLE_CUSTOMER') {
            this.isCustomer = true;
        }
        if ( role.authority === 'ROLE_ADMIN') {
          this.isAdmin = true;
        }
        if ( role.authority === 'ROLE_EDITOR') {
          this.isEditor = true;
        }
        if ( role.authority === 'ROLE_EDITORINCHIEF') {
          this.isEditorInChief = true;
        }
        if ( role.authority === 'ROLE_BUYER') {
          this.isBuyer = true;
        }
        if ( role.authority === 'ROLE_AUTHOR') {
          this.isAuthor = true;
        }
      }
    } else {
      this.isVistor = true;
    }
  }

  public getRoles(): Role[] {
    if (sessionStorage.getItem(ROLES_KEY)) {
      return JSON.parse(sessionStorage.getItem(ROLES_KEY));
    }
    return null;
  }

  public getToken(): string {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  public getUsername(): string {
    return sessionStorage.getItem(USERNAME_KEY);
  }

  public checkIsLoggedIn(): Boolean {
    // TODO: Promeniti da proveri vazi li token i dalje
    if (sessionStorage.getItem(TOKEN_KEY)) {
      return true;
    } else {
      return false;
    }
  }
}
