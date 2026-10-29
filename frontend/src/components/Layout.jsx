import { NavLink, Outlet } from 'react-router-dom'

const links = [
  ['/', 'Dashboard'],
  ['/crop-families', 'Crop families'],
  ['/grow-towers', 'Grow towers'],
  ['/growers', 'Growers'],
  ['/crop-batches', 'Crop batches'],
  ['/planting-cycles', 'Planting cycles'],
  ['/harvest-records', 'Harvest records'],
]

export default function Layout() {
  return (
    <div className="shell">
      <nav className="nav">
        <h1>Vertical Farming Web</h1>
        {links.map(([to, label]) => (
          <NavLink key={to} to={to} end={to === '/'}>
            {label}
          </NavLink>
        ))}
      </nav>
      <main className="content">
        <Outlet />
      </main>
    </div>
  )
}
