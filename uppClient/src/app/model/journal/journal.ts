import { AcademicField } from '../user/academicField';
import { Paper } from './paper';
enum MembershipFeeMethod {
    READERS = 0,
    AUTHORS = 1,
}

export class Journal {
    id: number;
    name: string;
    issn: string;
    journalAcademicFields: AcademicField[];
    membershipFeeMethod: MembershipFeeMethod;
    papers: Paper[] = [];
}