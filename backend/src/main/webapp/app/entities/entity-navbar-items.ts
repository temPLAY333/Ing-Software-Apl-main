import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Publisher',
    route: '/publisher',
    translationKey: 'global.menu.entities.publisher',
  },
  {
    name: 'Author',
    route: '/author',
    translationKey: 'global.menu.entities.author',
  },
  {
    name: 'Client',
    route: '/client',
    translationKey: 'global.menu.entities.client',
  },
  {
    name: 'Book',
    route: '/book',
    translationKey: 'global.menu.entities.book',
  },
  {
    name: 'BorrowedBook',
    route: '/borrowed-book',
    translationKey: 'global.menu.entities.borrowedBook',
  },
];
