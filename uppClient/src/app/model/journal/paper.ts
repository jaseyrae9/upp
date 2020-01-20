import { AcademicField } from '../user/academicField';
import { Journal } from './journal';

export class Paper {
    id: number;
    name: string;
    academicField: AcademicField;
    price: number;
    journal: Journal;
}