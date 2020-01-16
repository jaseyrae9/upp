import { AcademicField } from './academicField';

export class User {
    id: number;
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    city: string;
    country: string;
    title: string;
    // naucne oblasti koje izabere prilikom registracije
    academicFields: AcademicField[] = [];

    wantsToBeReviewer: boolean;
    active: boolean;
    acceptedAsReviewer: boolean;
}