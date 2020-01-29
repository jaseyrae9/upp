import { AcademicField } from '../user/academicField';
import { Paper } from './paper';
import { Editor } from '../user/editor';
enum MembershipFeeMethod {
    READERS = 0,
    AUTHORS = 1,
}

export class Journal {
    id: number;
    name: string;
    issn: string;
    academicFields: AcademicField[];
    membershipFeeMethod: MembershipFeeMethod;
    papers: Paper[] = [];
    editorInChief: Editor;
    price: number;
}
