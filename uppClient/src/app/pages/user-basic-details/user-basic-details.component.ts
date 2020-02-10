import { Component, OnInit, Input } from '@angular/core';
import { UserDTO } from 'src/app/model/user/userDto';
import { AdminService } from 'src/app/services/admin/admin.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-user-basic-details',
  templateUrl: './user-basic-details.component.html',
  styleUrls: ['./user-basic-details.component.css']
})
export class UserBasicDetailsComponent implements OnInit {
  @Input() user: UserDTO;
  isAdmin = false;
  isEditor = false;
  errorMessage = '';
  constructor(private adminService: AdminService) { }

  ngOnInit() {
    for (const authority of this.user.authorities) {
        if(authority.name === 'ADMIN') {
            this.isAdmin = true;
        }
        if(authority.name === 'EDITOR') {
          this.isEditor = true;
        }
    }
  }

  makeAdmin() {
    console.log('make admin: ', this.user.id);
    this.adminService.makeAdmin(this.user.id).subscribe(
      data => {
        console.log(data);
        alert('Uspešno ste dodali korisniku ulogu administratora.');
        window.location.reload();      },
      (err: HttpErrorResponse) => {
        this.errorMessage = err.error.message;
       console.log(err);
      }
    );


  }

  makeEditor() {
    console.log('make editor: ' + this.user.username);
    this.adminService.makeEditor(this.user.id).subscribe(
      data => {
        console.log(data);
        alert('Uspešno ste dodali korisniku ulogu urednika.');
        window.location.reload();      },
      (err: HttpErrorResponse) => {
       console.log(err);
       this.errorMessage = err.error.message;

      }
    );
  }
}
