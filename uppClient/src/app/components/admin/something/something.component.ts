import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from 'src/app/auth/token-storage.service';
import { RepositoryService } from 'src/app/services/repository.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-something',
  templateUrl: './something.component.html',
  styleUrls: ['./something.component.css']
})
export class SomethingComponent implements OnInit {
  message: string;

  private processInstance = '';
  private tasks = [];

  private formFieldsDto = null;
  private formFields = [];
  private enumKeys = [];
  private enumValues = [];

  constructor(public tokenService: TokenStorageService,
              private repositoryService: RepositoryService,
              private dataService: DataService) {

    this.repositoryService.getTasks().subscribe(
      res => {
        console.log('res: ', res);
        this.tasks = res;
      },
      err => {
        console.log('Error occured');
      }
    );
  }

  ngOnInit() {
  }

}
