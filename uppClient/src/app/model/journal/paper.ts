import { AcademicField } from '../user/academicField';
import { Journal } from './journal';
import { Author } from '../user/author';

export class Paper {
    id: number;
    name: string;
    academicField: AcademicField;
    journal: Journal;
    author: Author;
}
