import { Authority } from './authority';

export class UserDTO {
    id: number;
    username: String;
    firstName: String;
    lastName: String;
    authorities: Authority[] = [];

}
