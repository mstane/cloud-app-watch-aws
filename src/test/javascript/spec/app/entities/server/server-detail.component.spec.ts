import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CloudappwatchTestModule } from '../../../test.module';
import { ServerDetailComponent } from 'app/entities/server/server-detail.component';
import { Server } from 'app/shared/model/server.model';

describe('Component Tests', () => {
  describe('Server Management Detail Component', () => {
    let comp: ServerDetailComponent;
    let fixture: ComponentFixture<ServerDetailComponent>;
    const route = ({ data: of({ server: new Server(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CloudappwatchTestModule],
        declarations: [ServerDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ServerDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ServerDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.server).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
