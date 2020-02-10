import { AcademicField } from '../user/academicField';
import { Author } from '../user/author';
import { Edition } from './edition';

export class Paper {
    id: number;
    name: string;
    academicField: AcademicField;
    edition: Edition;
    author: Author;
}
