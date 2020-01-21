import { AcademicField } from '../user/academicField';
import { Journal } from './journal';
import { Author } from '../user/author';

export class Paper {
    id: number;
    name: string;
    academicField: AcademicField;
    price: number;
    journal: Journal;
    author: Author;
}