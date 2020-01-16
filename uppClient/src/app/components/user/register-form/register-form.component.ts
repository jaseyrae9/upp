import { Component, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { RepositoryService } from 'src/app/services/repository.service';
import { UserService } from 'src/app/services/users/user.service';
import { DataService } from 'src/app/services/data.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.css']
})
export class RegisterFormComponent implements OnInit {
  private formFieldsDto = null;
  private formFields = [];
  private processInstance = '';
  private enumKeys = [];
  private enumValues = [];
  errorMessage: String = '';

  message: string;

  constructor(private router: Router,
              private userService: UserService,
              private repositoryService: RepositoryService,
              private dataService: DataService) {

    repositoryService.startProcess().subscribe(
      res => {

        console.log(res);
        // this.categories = res;
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) => {
          if ( field.type.name === 'enum') {
            this.enumKeys = Object.keys(field.type.values);
            this.enumValues = Object.values(field.type.values);
            console.log('aaa: ' + this.enumValues);

          }
        });
        this.newMessage();
      },
      err => {
        console.log('Error occured');
        console.log(err);
      }
    );
  }

  ngOnInit() {
    this.dataService.currentMessage.subscribe(message => this.message = message);
    console.log('init u registraciji', this.message);

  }

  newMessage() {
    this.dataService.changeMessage(this.processInstance);
    console.log('new message u registraciji', this.message);
  }

  onSubmit(value, form) {
    console.log('kliknuto dugme registracija');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);
          o.push({fieldId : property, fieldValue : value[property]});
      }

      console.log(o);
      const x = this.userService.registerUser(o, this.formFieldsDto.taskId);
      x.subscribe(
        res => {
          console.log(res);
          this.router.navigate(['']);
          alert('Upešno ste se registrovali! Proverite email kako biste potvrdili registraciju' +
          ' i bili u mogućnosti da se prijavite na sistem. ');
        },
        (err: HttpErrorResponse) => {
          console.log('Error occured');
          console.log(err);
          this.errorMessage = err.error.message;

        }
      );
  }

}
