import { Component, OnInit, Input } from '@angular/core';
import { RepositoryService } from 'src/app/services/repository.service';
import { AdminService } from 'src/app/services/admin/admin.service';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
  selector: 'app-form-details',
  templateUrl: './form-details.component.html',
  styleUrls: ['./form-details.component.css']
})
export class FormDetailsComponent implements OnInit {
  @Input() taskId: string;
  private formFieldsDto = null;
  private formFields = [];
  private enumKeys = [];
  private enumValues = [];
  private processInstance = '';

  private labels = [];
  private names = [];
  private enumerations = [];
  private enumerationsValues = [];

  constructor(private repositoryService: RepositoryService,
              private adminService: AdminService) { }

  ngOnInit() {
    this.repositoryService.getForm(this.taskId).subscribe(
      res => {

        // this.categories = res;
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        console.log('this.formFields: ', this.formFields);
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) => {
          if ( field.type.name === 'enum') {
            this.labels.push(field.label);
            this.names.push(field.id);
            this.enumKeys = Object.keys(field.type.values);
            this.enumValues = Object.values(field.type.values);
            this.enumerations.push(this.enumKeys);
            this.enumerationsValues.push(this.enumValues);
            console.log('aaa: ' + this.enumValues);

          }
        });
      },
      err => {
        console.log('Error occured');
        console.log(err);
      }
    );

  }

  onSubmit(value, form) {
    console.log('kliknuto dugme odluci');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);
          o.push({fieldId : property, fieldValue : value[property]});
      }

      console.log(o);
      const x = this.adminService.decide(o, this.formFieldsDto.taskId);
      x.subscribe(
        res => {
          console.log(res);
          alert('Odlucio si!');
         //  window.location.reload();
        },
        err => {
          console.log('Error occured');
          console.log(err);
        }
      );
  }

}
