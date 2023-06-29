import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TeacherSubjectDetailComponent } from './teacher-subject-detail.component';

describe('TeacherSubject Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeacherSubjectDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TeacherSubjectDetailComponent,
              resolve: { teacherSubject: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(TeacherSubjectDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load teacherSubject on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TeacherSubjectDetailComponent);

      // THEN
      expect(instance.teacherSubject).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
