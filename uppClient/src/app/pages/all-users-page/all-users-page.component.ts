import { Component, OnInit } from '@angular/core';
import { AdminService } from 'src/app/services/admin/admin.service';
import { UserDTO } from 'src/app/model/user/userDto';

@Component({
  selector: 'app-all-users-page',
  templateUrl: './all-users-page.component.html',
  styleUrls: ['./all-users-page.component.css']
})
export class AllUsersPageComponent implements OnInit {
  users: UserDTO[] = [];
  isAdmin = [];
  isEditor = [];
  constructor(private adminService: AdminService) { }

  ngOnInit() {
    this.loadUsers();
  }


  loadUsers() {
    this.adminService.getAll().subscribe(data => {
      this.users = data;
      console.log('data: ', data);
      
    });
  }
}
