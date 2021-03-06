import { Component, OnInit } from '@angular/core';
import { JournalRepositoryService } from 'src/app/services/journal-repository.service';
import { JournalService } from 'src/app/services/journal/journal.service';
import { HttpErrorResponse } from '@angular/common/http';
import { JwtResponse } from 'src/app/auth/jwt-response';
import { UserService } from 'src/app/services/users/user.service';
import { TokenStorageService } from 'src/app/auth/token-storage.service';
import { Router } from '@angular/router';
import { TokenPayload } from 'src/app/model/token-payload';
import * as decode from 'jwt-decode';

@Component({
  selector: 'app-new-journal',
  templateUrl: './new-journal.component.html',
  styleUrls: ['./new-journal.component.css']
})
export class NewJournalComponent implements OnInit {

  private formFieldsDto = null;
  private formFields = [];
  private processInstance = '';
  private enumKeys = [];
  private enumValues = [];
  errorMessage: String = '';
  private enumKeys2 = [];
  private enumValues2 = [];


  constructor(private repositoryService: JournalRepositoryService,
              private journalService: JournalService,
              private userService: UserService,
              private tokenStorage: TokenStorageService,
              private router: Router) {
    repositoryService.startJournalProcess().subscribe(
      res => {

        console.log(res);
        // this.categories = res;
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) => {
          if ( field.type.name === 'enum') {
            if ( field.id === 'naucneOblasti') {
              this.enumKeys = Object.keys(field.type.values);
              this.enumValues = Object.values(field.type.values);
              console.log('aaa: ' + this.enumValues);
            }
            if ( field.id === 'nacinNaplateClanarine') {
              this.enumKeys2 = Object.keys(field.type.values);
              this.enumValues2 = Object.values(field.type.values);
              console.log('aaa2: ' + this.enumValues2);
            }
          }
        });
      },
      (err: HttpErrorResponse) => {
        console.log('error prilikom starta procesa za dodavanje casopisa');
        console.log('err: ', err);
        this.errorMessage = err.error.message;

      }
    );
   }

  ngOnInit() {
  }

  onSubmit(value, form) {
    console.log('kliknuto dugme za dodavanje casopisa');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);
          o.push({fieldId : property, fieldValue : value[property]});
      }

      console.log(o);
      this.journalService.addJournal(o, this.formFieldsDto.taskId).subscribe(
        (data: JwtResponse) => {
            this.tokenStorage.saveToken(data.token);
            const tokenPayload: TokenPayload = decode(data.token);
            this.tokenStorage.saveRoles(tokenPayload.roles);
            console.log(tokenPayload);
            alert('Uspešno ste dodali časopis!');
            this.router.navigate(['']);
        },
        (err: HttpErrorResponse) => {
          console.log('Error prilikom dodavanje casopisa');
          console.log('err: ', err);
          this.errorMessage = err.error.message;

        }
      );
  }

}
