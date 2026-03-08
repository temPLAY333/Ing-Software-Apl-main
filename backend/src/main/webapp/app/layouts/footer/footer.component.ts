import { Component } from '@angular/core';
import { TranslateDirective } from 'app/shared/language';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  imports: [TranslateDirective, FaIconComponent],
})
export default class FooterComponent {}
