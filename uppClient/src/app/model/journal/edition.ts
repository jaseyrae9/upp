import { AcademicField } from '../user/academicField';
import { Paper } from './paper';
import { Editor } from '../user/editor';
import { Journal } from './journal';

export class Edition {
    id: number;
    number: number;
    published: boolean;
    journal: Journal = new Journal();
    papers: Paper[] = [];
    date: Date = new Date();
}

